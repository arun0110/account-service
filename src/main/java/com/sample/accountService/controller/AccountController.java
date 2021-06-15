package com.sample.accountService.controller;

import com.sample.accountService.config.JWTUtility;
import com.sample.accountService.model.JwtRequest;
import com.sample.accountService.model.JwtResponse;
import com.sample.accountService.model.Member;
import com.sample.accountService.service.UserService;
import com.sample.accountService.utility.DataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class AccountController {

    @Autowired
    private JWTUtility jwtUtility;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @GetMapping("/ping")
    public String ping(){

        return "Account Service is Up!!!";
    }

    @PostMapping("authenticate")
    public JwtResponse authenticate(@RequestBody JwtRequest jwtRequest) throws Exception{
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUserName(),
                    jwtRequest.getPassword()
            ));
        }catch (BadCredentialsException ex){
            throw new BadCredentialsException("INVALID CREDENTIALS");
        }

        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUserName());
        String token = jwtUtility.generateToken(userDetails);
        return new JwtResponse(token);
    }

    @PostMapping("register")
    public String userRegistration(@RequestBody Member member){
        if(Objects.nonNull(DataSet.memberMap) && DataSet.memberMap.size()>0){
            if(DataSet.memberMap.containsKey(member.getUserName())){
                return "Already User Exists";
            }
        }
        DataSet.memberMap.put(member.getUserName(), member);
        return "Registration Success!";
    }

    @GetMapping("user-list")
    public List<Member> getAllMembers(){
        return DataSet.memberMap.values().stream().collect(Collectors.toList());
    }
}
