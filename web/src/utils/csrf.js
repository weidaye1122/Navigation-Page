export function getCsrfHeaders() {
  const token = getCookie('XSRF-TOKEN')
  const headers = { 'X-Requested-With': 'XMLHttpRequest' }
  if (token) {
    headers['X-XSRF-TOKEN'] = token
  }
  return headers
}

function getCookie(name) {
  const prefix = `${name}=`
  const cookies = document.cookie ? document.cookie.split(';') : []
  for (const cookie of cookies) {
    const value = cookie.trim()
    if (value.startsWith(prefix)) {
      return decodeURIComponent(value.substring(prefix.length))
    }
  }
  return ''
}
