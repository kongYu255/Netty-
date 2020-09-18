package com.yuy.runCodeOnDocker.mapper;

import com.yuy.entity.CodeEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CodeMapper {

    @Insert("insert into code(member_id, content, type) values(#{memberId}, #{content}, #{type})")
    int save(CodeEntity entity);


    @Select("select content from code where member_id = #{memberId}")
    String getCodeByMemberId(@Param("memberId") Integer memberId);
}
