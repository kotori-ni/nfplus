/*
 * @Description: 用户收藏控制类 
 * @Author: wch
 * @email: 1301457114@qq.com
 * @Date: 2023-07-29 21:07:41
 * @LastEditors: wch
 * @LastEditTime: 2023-08-15 14:30:35
 */

package com.example.nfplus.controller;

import com.example.nfplus.entity.Collection;
import com.example.nfplus.entity.User;
import com.example.nfplus.service.CollectionService;
import com.example.nfplus.service.UserService;
import com.example.nfplus.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;
    @Autowired
    private UserService userService;

    /**
     * @description: 添加收藏
     * @param token      用户token
     * @param collection 待添加的收藏信息
     * @return ResultUtils
     * @author: wch
     */
    @PostMapping("/add")
    public ResultUtils addCollection(@RequestHeader("Authorization") String token, @RequestBody Collection collection) {
        try {
            User user = userService.findUserByToken(token);
            collection.setUserId(user.getUserId());
            collectionService.verifyCollection(collection);
            if (collectionService.save(collection))
                return ResultUtils.ok().message("添加收藏成功");
            return ResultUtils.error().message("添加收藏失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("添加收藏失败");
        }
    }

    /**
     * @description: 删除收藏
     * @param token      用户token
     * @param collection
     * @return ResultUtils
     * @author: wch
     */
    @DeleteMapping("delete")
    public ResultUtils deleteCollection(@RequestHeader("Authorization") String token,
            @RequestBody Collection collection) {
        try {
            User user = userService.findUserByToken(token);
            collection.setUserId(user.getUserId());
            collectionService.verifyCollection(collection);
            if (collectionService.removeCollection(collection))
                return ResultUtils.ok().message("取消收藏成功");
            return ResultUtils.error().message("取消收藏失败");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResultUtils.error().message(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.error().message("取消收藏失败");
        }
    }
}
