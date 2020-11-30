package com.example.demo.controller;

import com.example.demo.entity.DirInf;
import com.example.demo.entity.FileInf;
import com.example.demo.entity.UserInf;
import com.example.demo.service.CheckPermissionsService;
import com.example.demo.service.DirInfService;
import com.example.demo.service.FileInfServive;
import com.example.demo.util.PreviewUtil;
import com.example.demo.util.ResponseUtil;
import com.example.demo.util.ZipCompress;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;
import org.apache.commons.io.IOUtils;
import org.jodconverter.DocumentConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Controller
public class FileInfController {

    @Autowired
    FileInfServive fileInfServive;
    @Autowired
    DirInfService dirInfService;
    @Autowired
    CheckPermissionsService checkPermissionsService;

    /*
    文件上传
    */
    @ResponseBody
    @RequestMapping("/fileUpload")
    public String fileUpload(MultipartFile uploadfile, Integer dirId, HttpSession session){
        UserInf userInf = (UserInf) session.getAttribute("USER_SESSION");

        //如果文件夹所有者不是当前用户，检查用户是否有该操作权限，如果没有则返回提示
        DirInf dirInf = dirInfService.selectByPrimaryKey(dirId);
        if(dirInf.getUserId() != userInf.getUserId()){
            //检查上传权限，如果返回为false；
             if(checkPermissionsService.checkUploadPremission(userInf))
                 fileInfServive.fileUpload(uploadfile,dirId,userInf);
             return "No Access";
       }

        //上传服务
        fileInfServive.fileUpload(uploadfile,dirId,userInf);
        return "OK";
    }

    /*
    文件下载
    */
    @ResponseBody
    @RequestMapping(value = "/download")
    public void download(Integer fileId,Integer dirId,HttpServletResponse response,HttpSession session) throws Exception {
        DirInf dirInf = dirInfService.selectByPrimaryKey(dirId); //文件夹信息
        UserInf userInf = (UserInf) session.getAttribute("USER_SESSION"); //用户信息
        //判断下载行为；
        if(dirInf.getUserId() != userInf.getUserId()){
            //该用户不是这个文件或文件夹的所有者，进行权限检查
            if(checkPermissionsService.checkDownloadPremission(userInf))
                fileInfServive.download(fileId, dirId, response, session); //拥有权限则下载

            //没有权限则输出提示
            response.setContentType("text/html;charset=utf-8");  //
            //输出流
            OutputStream os = response.getOutputStream();;  //输出流
            os.write("<script> alert('您没有下载权限!'); window.history.back();</script>".getBytes());
            os.close();
            os.flush();
//
        }

        //用户为文件或文件夹所有者则直接下载
        fileInfServive.download(fileId,dirId,response,session);

        }



    /*
    文件删除
    */
    @ResponseBody
    @RequestMapping("/deleteFile")
    public String deleterFile(Integer fileId, HttpSession session){
        if (fileInfServive.deleteByPrimaryKey(fileId))  return "OK";
        return "FALSE";
    }

    /*文件预览实现：
        文件id、文件夹id->获取文件的绝对路径->...
    */
    @ResponseBody
    @RequestMapping("preview")
    public String toPdfFile(Integer fileId, HttpServletResponse response, HttpSession session) throws IOException {


        //获取实例
        UserInf userInf = (UserInf) session.getAttribute("USER_SESSION"); //用户信息
        FileInf fileInf = fileInfServive.selectByPrimaryKey(fileId);//文件信息
        DirInf dirInf = dirInfService.selectByPrimaryKey(fileInf.getDirId());//文件夹

        //检查预览权限，
        if(dirInf.getUserId() != userInf.getUserId()){
            //检查上传权限，如果返回为false；
            if(checkPermissionsService.checkPreviewPremission(userInf))
                    return fileInfServive.preview(fileId,response);
            //return "No Access"
            //没有权限则输出提示
            response.setContentType("text/html;charset=utf-8");  //
            //输出流
            OutputStream os = response.getOutputStream();;  //输出流
            os.write("<script> alert('您没有预览权限!'); window.close();</script>".getBytes());
            os.close();
            os.flush();
        }

        return fileInfServive.preview(fileId,response);
    }

}
