//package com.cqupt.art.auth;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.cqupt.art.entity.DefaultUserDetail;
//import com.cqupt.art.entity.User;
//import com.cqupt.art.mapper.PmUserMapper;
//import com.cqupt.art.auth.DefaultUserDetailMapper;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class DefaultUserDetailService implements UserDetailsService {
//
//    @Autowired
//    PmUserMapper userMapper;
//    @Autowired
//    DefaultUserDetailMapper defaultUserDetailMapper;
//    @Override
//    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        User user = userMapper.selectOne(new QueryWrapper<User>().eq("user_phone", s));
//        if(user==null){
//            throw new RuntimeException("用户不存在!");
//        }
//        List<String> roles = defaultUserDetailMapper.findRoleByUserName(s);
//        List<String> permissions = defaultUserDetailMapper.findPermissionByRoleCodes(roles);
//        roles = roles.stream().map(rc->{
//            return "RC_"+rc;
//        }).collect(Collectors.toList());
//        permissions.addAll(roles);
//        DefaultUserDetail userDetail = new DefaultUserDetail();
//        BeanUtils.copyProperties(user,userDetail);
//        userDetail.setAuthorities(AuthorityUtils.commaSeparatedStringToAuthorityList(
//                String.join(",",permissions)
//        ));
//        return userDetail;
//    }
//}
