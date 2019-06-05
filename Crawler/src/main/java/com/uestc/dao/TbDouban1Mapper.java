package com.uestc.dao;

import com.uestc.domain.TbDouban1;
import com.uestc.domain.TbDouban1Example;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TbDouban1Mapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    long countByExample(TbDouban1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    int deleteByExample(TbDouban1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    int insert(TbDouban1 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    int insertSelective(TbDouban1 record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    List<TbDouban1> selectByExample(TbDouban1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    int updateByExampleSelective(@Param("record") TbDouban1 record, @Param("example") TbDouban1Example example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table douban1
     *
     * @mbg.generated Wed Jun 05 13:45:01 GMT+08:00 2019
     */
    int updateByExample(@Param("record") TbDouban1 record, @Param("example") TbDouban1Example example);
}