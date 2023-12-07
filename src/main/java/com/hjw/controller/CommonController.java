package com.hjw.controller;

import com.hjw.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController
{
    @Value("${reggie.path}")
    private String path;

    // 文件上传
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file)
    {


        // file是一个临时文件，需要转存到指定路径，否则请求完成之后就会被删除

        // 1、获取原始文件的后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 2、使用UUID生成name
        String fileName = UUID.randomUUID().toString() +suffix;

        // 需要判断目录 path是否存在，如果不存在则创建
        File dir = new File(path);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        try
        {
            // 转存到指定路径 (getOriginalFilename获取原始文件名)
            file.transferTo(new File(path + fileName));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return Result.success(fileName);
    }

    // 文件下载
    @GetMapping("download")
    public void download(String name, HttpServletResponse response)
    {
        try
        {
            // 输入流，读取文件内容
            FileInputStream fis = new FileInputStream(path + name);

            // 输出流，将文件写会浏览器
            ServletOutputStream ops = response.getOutputStream();
            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fis.read(bytes)) != -1)
            {
                ops.write(bytes, 0, len);
                ops.flush();
            }

            ops.close();
            fis.close();


        }
        catch (IOException e)
        {
            log.error(e.getMessage());
        }


    }

}
