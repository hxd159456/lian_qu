//package com.cqupt.art.auth;
//
//import com.cqupt.art.entity.DefaultUserDetail;
//import org.apache.ibatis.annotations.Mapper;
//import org.apache.ibatis.annotations.Param;
//import org.apache.ibatis.annotations.Select;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Mapper
//public interface DefaultUserDetailMapper {
////    @Select("select username,password,enabled from sys_user u where u.username=#{userId}")
////    DefaultUserDetail findByUserName(@Param("userId") String userId);
//    @Select("SELECT role_code from sys_role r \n" +
//            "left join sys_user_role ur on r.id=ur.role_id\n" +
//            "left join pm_user u on u.user_id=ur.user_id\n" +
//            "where u.user_phone = #{userName}")
//    List<String> findRoleByUserName(@Param("userName") String userName);
//
//    @Select({"<script>","SELECT DISTINCT uri from sys_api_permission p " ,
//            "LEFT JOIN sys_role_api_permissions rp on p.id = rp.api_permission_id " ,
//            "left join sys_role r on r.id = rp.role_id" ,
//            "where r.role_code in " ,
//            "<foreach  collection='roleCodes' item='roleCode' open='(' separator=',' close=')'>" ,
//            "#{roleCode}",
//            "</foreach>",
//            "</script>"})
//    List<String> findPermissionByRoleCodes(@Param("roleCodes") List<String> roleCodes);
//}
