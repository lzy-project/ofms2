package com.example.demo.service;

import com.example.demo.entity.DirInf;
import com.example.demo.entity.UserInf;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DirInfService {
    //查询
    DirInf selectRootChildrenDirByUserId(Integer userId,String dirName);
    DirInf selectRootDirByUserId(Integer userId);         //根文件夹
    DirInf selectByPrimaryKey(Integer dirId);
    DirInf selectDirByFileId(Integer fileId);
    List<DirInf> selectByDirName(String dirName,Integer dirId);
    List<DirInf> selectDirListByDirId(Integer dirId);
    List<DirInf> selectParentDirByDirId(Integer dirId);
    List<DirInf> selectChildrenDirByDirId(Integer dirId);

    //插入
    int insertSelective(String dirName, Integer dirId, UserInf userId);

    //删除
    Boolean deleteByPrimaryKey(Integer dirId);
}
