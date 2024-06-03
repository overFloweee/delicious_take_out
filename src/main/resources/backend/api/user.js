function getUserList (params) {
    return $axios({
        url: '/user/page',
        method: 'get',
        params
    })
}

// 修改---添加员工
function editUser (params) {
    return $axios({
        url: '/user',
        method: 'put',
        data: { ...params }
    })
}

// 新增---添加员工
function addUser (params) {
    return $axios({
        url: '/user',
        method: 'post',
        data: { ...params }
    })
}

// 修改页面反查详情接口
function queryUserById (id) {
    return $axios({
        url: `/user/${id}`,
        method: 'get'
    })
}
