package com.example.demo.controller;

import com.example.demo.aop.ArchivesLog;
import com.example.demo.entity.DirInf;
import com.example.demo.entity.Team;
import com.example.demo.entity.UserInf;
import com.example.demo.service.DirInfService;
import com.example.demo.service.FileCabinetService;
import com.example.demo.service.TeamService;
import com.example.demo.service.UserInfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Controller
public class LoginAndRegisterController {
    @Autowired
    UserInfService userInfService;
    @Autowired
    DirInfService dirInfService;
    @Autowired
    FileCabinetService fileCabinetService;
    @Autowired
    TeamService teamService;

    /*
     登录
     */
    @RequestMapping(value = "/userLogin",method = RequestMethod.POST)
    @ArchivesLog(operteContent = "登录系统")
    public String login(String email, String password, Map<String,Object> map, HttpSession session) {
        // 通过账号和密码查询用户
        UserInf user = userInfService.verifyLogin(email,password);//验证登录信息

        //验证用户
        if(user != null){
            // 将用户对象添加到Session
            session.setAttribute("USER_SESSION", user);
            System.out.println("登录成功！");
            // 跳转到主页面
            return "redirect:/menu";
        }
        //验证失败
        String msg =  "账号或密码错误，请重新输入";
        map.put("msg",msg);
        return "login";
    }

    /*
      管理员登录
 */
    @RequestMapping(value = "/adminstrationLogin",method = RequestMethod.POST)
    public String adminstrationLogin(String username,String password, Model model, HttpSession session) {
        // 通过账号和密码查询用户
        UserInf user = userInfService.selectByUserName(username);//验证登录信息

        System.out.println("账号："+user);
        //验证用户
        if(user != null & user.getUserType() == 0 & user.getPassword().equals(password)){
            // 将用户对象添加到Session
            session.setAttribute("USER_SESSION", user);
            //跳转到管理页面
            return "redirect:/adminstrationMenu";
        }
        //验证失败
        model.addAttribute("msg", "账号或密码错误，请重新确认！");
        return "adminstration_login";
    }


    /*
     注册
     */
    @ResponseBody
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String toRgister(UserInf userInf, Team team) {

        System.out.println(userInf.toString());

        //调用注册服务，再用户表注册该用户
        UserInf newUser =  userInfService.insertSelective(userInf);
        //调用创建文件服务，创建根文件夹和’我的文件‘
        DirInf rootDir = dirInfService.insertSelective(null,null,newUser); //创建根文件
        DirInf myDir = dirInfService.insertSelective("我的文件",rootDir.getDirId(),newUser);  //创建我的文件

        //创建我的文件，文件柜
        fileCabinetService.newFileCabinet(myDir,userInf);

        //如果是团队账号，创建团队
        if(userInf.getUserType() == 2)  teamService.insertSelective(team,userInf);

        return "OK";
    }
    /*
     退出登录
     */
    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }


}
