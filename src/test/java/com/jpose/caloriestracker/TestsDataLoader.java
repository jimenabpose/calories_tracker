package com.jpose.caloriestracker;

import java.util.Arrays;
import java.util.Collection;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.jpose.caloriestracker.entity.AppUser;
import com.jpose.caloriestracker.entity.CaloriesRecord;
import com.jpose.caloriestracker.entity.Privilege;
import com.jpose.caloriestracker.entity.Role;
import com.jpose.caloriestracker.repository.CaloriesRecordRepository;
import com.jpose.caloriestracker.repository.PrivilegeRepository;
import com.jpose.caloriestracker.repository.RoleRepository;
import com.jpose.caloriestracker.repository.UserRepository;

@Component
public class TestsDataLoader implements ApplicationListener<ContextRefreshedEvent> {
 
    private boolean alreadySetup = false;
 
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;
    @Autowired
    private CaloriesRecordRepository caloriesRecordRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) {
        	return;
        }
        
        Privilege userCrudOthersPrivilege = createPrivilegeIfNotFound(Privilege.USER_CRUD_OTHERS);
        Privilege userCrudOwnPrivilege = createPrivilegeIfNotFound(Privilege.USER_CRUD_OWN);
        Privilege caloriesRecordCrudOthersPrivilege = createPrivilegeIfNotFound(Privilege.CALORIES_RECORD_CRUD_OTHERS);
        Privilege caloriesRecordCrudOwnPrivilege = createPrivilegeIfNotFound(Privilege.CALORIES_RECORD_CRUD_OWN);
        Role adminRole = createRoleIfNotFound("ROLE_ADMIN", Arrays.asList(userCrudOthersPrivilege, userCrudOwnPrivilege, caloriesRecordCrudOthersPrivilege, caloriesRecordCrudOwnPrivilege));
        Role userRole = createRoleIfNotFound("ROLE_USER", Arrays.asList(userCrudOwnPrivilege, caloriesRecordCrudOwnPrivilege));
        Role userManagerRole = createRoleIfNotFound("ROLE_USER_MANAGER", Arrays.asList(userCrudOthersPrivilege, userCrudOwnPrivilege));
 
        AppUser adminUser = new AppUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.setRoles(Arrays.asList(adminRole));
        adminUser.setEnabled(true);
        userRepository.save(adminUser);
        
        AppUser user = new AppUser();
        user.setUsername("usermanager");
        user.setPassword(passwordEncoder.encode("usermanager"));
        user.setRoles(Arrays.asList(userManagerRole));
        user.setEnabled(true);
        userRepository.save(user);
        
        user = new AppUser();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("user"));
        user.setRoles(Arrays.asList(userRole));
        user.setEnabled(true);
        userRepository.save(user);
        
        AppUser user2 = new AppUser();
        user2.setUsername("user2");
        user2.setPassword(passwordEncoder.encode("user2"));
        user2.setRoles(Arrays.asList(userRole));
        user2.setEnabled(true);
        userRepository.save(user2);
        
        LocalDate date = new LocalDate(2016, 04, 25);
		Long calories = 1000L;
    	CaloriesRecord caloriesRecord = new CaloriesRecord();
    	caloriesRecord.setDate(date);
    	caloriesRecord.setText("caloriesRecord1");
    	caloriesRecord.setCaloriesQuantity(calories);
    	caloriesRecord.setTime(new LocalTime(12, 30));
    	caloriesRecord.setUser(user);
    	caloriesRecordRepository.save(caloriesRecord);
    	
    	caloriesRecord = new CaloriesRecord();
    	caloriesRecord.setDate(date);
    	caloriesRecord.setText("caloriesRecord2");
    	caloriesRecord.setCaloriesQuantity(calories + 50);
    	caloriesRecord.setTime(new LocalTime(21, 00));
    	caloriesRecord.setUser(user);
    	caloriesRecordRepository.save(caloriesRecord);
    	
    	caloriesRecord = new CaloriesRecord();
    	caloriesRecord.setDate(date);
    	caloriesRecord.setText("caloriesRecord3");
    	caloriesRecord.setCaloriesQuantity(calories + 50);
    	caloriesRecord.setTime(new LocalTime(21, 00));
    	caloriesRecord.setUser(user);
    	caloriesRecordRepository.save(caloriesRecord);
    }
 
    @Transactional
    private Privilege createPrivilegeIfNotFound(String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }
 
    @Transactional
    private Role createRoleIfNotFound(String name, Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}