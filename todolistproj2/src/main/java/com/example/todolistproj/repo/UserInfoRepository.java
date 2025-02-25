package com.example.todolistproj.repo;

import com.example.todolistproj.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 继承JpaRepository后就可以使用常规的数据库操作（增删查改）
 */
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    public UserInfo findByUsername(String username);

    UserInfo findByUsernameAndPassword(String username, String password);
}
