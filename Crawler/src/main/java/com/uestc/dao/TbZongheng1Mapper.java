package com.uestc.dao;

import com.uestc.domain.TbZongheng1;
import com.uestc.domain.TbZongheng1Example;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
public interface TbZongheng1Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    long countByExample(TbZongheng1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    int deleteByExample(TbZongheng1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    int insert(TbZongheng1 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    int insertSelective(TbZongheng1 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    List<TbZongheng1> selectByExample(TbZongheng1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    int updateByExampleSelective(@Param("record") TbZongheng1 record, @Param("example") TbZongheng1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table zongheng1
     *
     * @mbg.generated Sun Jun 02 10:18:12 GMT+08:00 2019
     */
    int updateByExample(@Param("record") TbZongheng1 record, @Param("example") TbZongheng1Example example);
}