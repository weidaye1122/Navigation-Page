package com.tuituidan.openhub.service;

import com.tuituidan.openhub.bean.dto.ChangePassword;
import com.tuituidan.openhub.bean.dto.UserDto;
import com.tuituidan.openhub.bean.entity.Role;
import com.tuituidan.openhub.bean.entity.RoleUser;
import com.tuituidan.openhub.bean.entity.User;
import com.tuituidan.openhub.bean.entity.UserStar;
import com.tuituidan.openhub.bean.vo.UserVo;
import com.tuituidan.openhub.consts.Consts;
import com.tuituidan.openhub.repository.RoleRepository;
import com.tuituidan.openhub.repository.RoleUserRepository;
import com.tuituidan.openhub.repository.UserRepository;
import com.tuituidan.openhub.repository.UserStarRepository;
import com.tuituidan.openhub.util.BeanExtUtils;
import com.tuituidan.openhub.util.ListUtils;
import com.tuituidan.openhub.util.StringExtUtils;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * UserService.
 *
 * @author tuituidan
 * @version 1.0
 * @date 2022/11/16
 */
@Service
public class UserService implements UserDetailsService {

    @Resource
    private UserRepository userRepository;

    @Resource
    private RoleRepository roleRepository;

    @Resource
    private RoleUserRepository roleUserRepository;

    @Resource
    private UserStarRepository userStarRepository;

    @Resource
    private CacheService cacheService;

    /**
     * 是否需要初始化管理员.
     *
     * @return boolean
     */
    public boolean needsSetup() {
        return userRepository.count() == 0;
    }

    /**
     * 初始化第一个管理员.
     *
     * @param userDto userDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void initializeAdmin(UserDto userDto) {
        Assert.isTrue(needsSetup(), "管理员已初始化，请直接登录");
        validateUser(userDto);
        initDefaultRole();
        userRepository.save(new User().setId(Consts.DEFAULT_ID)
                .setUsername(userDto.getUsername())
                .setStatus(Consts.DEFAULT_ID)
                .setNickname(userDto.getNickname())
                .setAvatar("/assets/images/header.png")
                .setPassword(encodePassword(userDto.getPassword(), userDto.getConfirmPassword())));
        roleUserRepository.save(new RoleUser().setId(Consts.DEFAULT_ID)
                .setRoleId(Consts.DEFAULT_ID).setUserId(Consts.DEFAULT_ID));
    }

    private void initDefaultRole() {
        if (roleRepository.existsById(Consts.DEFAULT_ID)) {
            return;
        }
        roleRepository.save(new Role().setId(Consts.DEFAULT_ID).setRoleName("管理员"));
    }

    /**
     * 分页查询用户
     *
     * @param keywords keywords
     * @param pageIndex pageIndex
     * @param pageSize pageSize
     * @return Page
     */
    public Page<UserVo> selectPage(String keywords, Integer pageIndex, Integer pageSize) {
        Specification<User> search = (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            if (StringUtils.isNotBlank(keywords)) {
                predicate.getExpressions().add(builder.or(builder.like(root.get("username"), "%" + keywords + "%"),
                        builder.like(root.get("nickname"), "%" + keywords + "%")));
            }
            return predicate;
        };
        Page<User> users = userRepository.findAll(search,
                PageRequest.of(pageIndex, pageSize, Sort.by("updateTime").descending()));
        Map<String, List<Role>> userRolesMap =
                getUserRolesMap(users.stream().map(User::getId).collect(Collectors.toSet()));
        return users.map(user ->
                BeanExtUtils.convert(user, UserVo::new)
                        .setRoles(userRolesMap.get(user.getId()))
        );
    }

    private Map<String, List<Role>> getUserRolesMap(Collection<String> userIds) {
        List<RoleUser> roleUsers = roleUserRepository.findByUserIdIn(userIds);
        return roleUsers.stream().collect(Collectors.groupingBy(RoleUser::getUserId,
                Collectors.mapping(item -> cacheService.getRole(item.getRoleId()),
                        Collectors.toList())));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        Assert.isTrue("1".equals(user.getStatus()), "用户已被禁用");
        return user;
    }

    /**
     * 保存
     *
     * @param id id
     * @param userDto userDto
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(String id, UserDto userDto) {
        Assert.isTrue(!StringUtils.equals(Consts.DEFAULT_ID, id), "默认管理员不可操作");
        validateUser(userDto);
        User user;
        User exitUser = userRepository.findByUsername(userDto.getUsername());
        if (StringUtils.isBlank(id)) {
            Assert.isTrue(exitUser == null, "账号已存在");
            user = BeanExtUtils.convert(userDto, User::new);
            user.setId(StringExtUtils.getUuid());
            user.setPassword(encodePassword(userDto.getPassword(), userDto.getConfirmPassword()));
        } else {
            user = userRepository.findById(id).orElseThrow(NullPointerException::new);
            Assert.isTrue(exitUser == null || StringUtils.equals(exitUser.getId(), id), "账号已存在");
            BeanExtUtils.copyProperties(userDto, user, "nickname", "avatar", "username", "status", "email");
            roleUserRepository.deleteByUserId(user.getId());
        }
        if (ArrayUtils.isNotEmpty(userDto.getRoleIds())) {
            roleUserRepository.saveAll(Arrays.stream(userDto.getRoleIds())
                    .map(roleId -> new RoleUser().setId(StringExtUtils.getUuid()).setRoleId(roleId)
                            .setUserId(user.getId())).collect(Collectors.toList()));
        }
        userRepository.save(user);
    }

    private void validateUser(UserDto userDto) {
        Assert.notNull(userDto, "用户信息不能为空");
        Assert.hasText(userDto.getNickname(), "用户名不能为空");
        Assert.hasText(userDto.getUsername(), "账号不能为空");
    }

    private String encodePassword(String password, String confirmPassword) {
        Assert.hasText(password, "密码不能为空");
        Assert.isTrue(StringUtils.length(password) >= 6 && StringUtils.length(password) <= 100,
                "密码长度需在6到100个字符之间");
        Assert.isTrue(StringUtils.equals(password, confirmPassword), "两次输入的密码不一致");
        return new BCryptPasswordEncoder().encode(password);
    }

    /**
     * 删除
     *
     * @param id id
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String[] id) {
        List<String> ids = Arrays.asList(id);
        Assert.isTrue(!ids.contains(Consts.DEFAULT_ID), "默认管理员不可操作");
        userRepository.deleteAllById(Arrays.asList(id));
        roleUserRepository.deleteByUserIdIn(ids);
    }

    /**
     * changePassword
     *
     * @param userId userId
     * @param changePassword changePassword
     */
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String userId, ChangePassword changePassword) {
        Assert.notNull(changePassword, "密码信息不能为空");
        User user = userRepository.findById(userId).orElseThrow(NullPointerException::new);
        Assert.notNull(user, "用户获取失败");
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Assert.isTrue(passwordEncoder.matches(changePassword.getOldPassword(),
                user.getPassword()), "原密码不匹配");
        user.setPassword(encodePassword(changePassword.getNewPassword(), changePassword.getConfirmPassword()));
        userRepository.save(user);
    }

    /**
     * resetPassword
     *
     * @param ids ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String[] ids, ChangePassword changePassword) {
        Assert.notNull(changePassword, "密码信息不能为空");
        Assert.isTrue(!Arrays.asList(ids).contains(Consts.DEFAULT_ID), "默认管理员不可操作");
        String password = encodePassword(changePassword.getNewPassword(), changePassword.getConfirmPassword());
        List<User> users = userRepository.findAllById(Arrays.asList(ids));
        users.forEach(user -> user.setPassword(password));
        userRepository.saveAll(users);
    }

    /**
     * changeStatus
     *
     * @param id id
     * @param status status
     */
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(String id, String status) {
        Assert.isTrue(!StringUtils.equals(Consts.DEFAULT_ID, id), "默认管理员不可操作");
        User user = userRepository.findById(id).orElseThrow(NullPointerException::new).setStatus(status);
        userRepository.save(user);
    }

    /**
     * userStarCard
     *
     * @param userId userId
     * @param cardIds cardIds
     */
    public void userStarCard(String userId, String[] cardIds) {
        Set<String> existIds = userStarRepository.findByUserId(userId).stream()
                .map(UserStar::getCardId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(existIds)) {
            if (ArrayUtils.isEmpty(cardIds)) {
                return;
            }
            userStarRepository.saveAll(Arrays.stream(cardIds).map(cardId -> new UserStar().setUserId(userId)
                    .setCardId(cardId).setId(StringExtUtils.getUuid())).collect(Collectors.toList()));
            return;
        }
        Pair<Set<String>, Set<String>> savePair = ListUtils.compareSaveIds(cardIds, existIds);
        if (CollectionUtils.isNotEmpty(savePair.getLeft())) {
            userStarRepository.deleteByUserIdAndCardIdIn(userId, savePair.getLeft());
        }
        if (CollectionUtils.isNotEmpty(savePair.getRight())) {
            userStarRepository.saveAll(savePair.getRight().stream().map(cardId -> new UserStar().setUserId(userId)
                    .setCardId(cardId).setId(StringExtUtils.getUuid())).collect(Collectors.toList()));
        }
    }

}
