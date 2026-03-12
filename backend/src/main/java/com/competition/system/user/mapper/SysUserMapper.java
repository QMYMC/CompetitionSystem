package com.competition.system.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.competition.system.user.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    @Select("""
            SELECT r.role_code
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.deleted = 0
              AND ur.deleted = 0
              AND r.status = 1
            ORDER BY r.sort ASC, r.id ASC
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT c.college_name
            FROM sys_college c
            WHERE c.id = #{collegeId}
              AND c.deleted = 0
            """)
    String selectCollegeNameByCollegeId(@Param("collegeId") Long collegeId);
}
