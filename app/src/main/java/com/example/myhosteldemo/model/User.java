package com.example.myhosteldemo.model;

import java.io.Serializable;

public class User implements Serializable {
   //data members
   String username , email , password , profile , phone , enroll , branch , year , gender , dob ;
   boolean signedin , setuped ;

   //constructors
   public User() {
   }

   public User(String username, String email, String profile , boolean gmail) {
      this.username = username;
      this.email = email;
      this.profile = profile;
      password = "" ;
      phone = "" ;
      enroll = "" ;
      branch = "" ;
      year = "" ;
      gender = "" ;
      dob = "" ;
   }

   public User(String username, String email, String password) {
      this.username = username;
      this.email = email;
      this.password = password;
      profile = "" ;
      phone = "" ;
      enroll = "" ;
      branch = "" ;
      year = "" ;
      gender = "" ;
      dob = "" ;
   }

   public User(String username, String email, String password, String profile, String phone, String enroll, String branch, String year, String gender, String dob, boolean signedin, boolean setuped) {
      this.username = username;
      this.email = email;
      this.password = password;
      this.profile = profile;
      this.phone = phone;
      this.enroll = enroll;
      this.branch = branch;
      this.year = year;
      this.gender = gender;
      this.dob = dob;
      this.signedin = signedin;
      this.setuped = setuped;
   }

   //getters

   public String getUsername() {
      return username;
   }

   public String getEmail() {
      return email;
   }

   public String getPassword() {
      return password;
   }

   public String getProfile() {
      return profile;
   }

   public String getPhone() {
      return phone;
   }

   public String getEnroll() {
      return enroll;
   }

   public String getBranch() {
      return branch;
   }

   public String getYear() {
      return year;
   }

   public String getGender() {
      return gender;
   }

   public String getDob() {
      return dob;
   }

   public boolean isSignedin() {
      return signedin;
   }

   public boolean isSetuped() {
      return setuped;
   }

   //setters

   public void setUsername(String username) {
      this.username = username;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setProfile(String profile) {
      this.profile = profile;
   }

   public void setPhone(String phone) {
      this.phone = phone;
   }

   public void setEnroll(String enroll) {
      this.enroll = enroll;
   }

   public void setBranch(String branch) {
      this.branch = branch;
   }

   public void setYear(String year) {
      this.year = year;
   }

   public void setGender(String gender) {
      this.gender = gender;
   }

   public void setDob(String dob) {
      this.dob = dob;
   }

   public void setSignedin(boolean signedin) {
      this.signedin = signedin;
   }

   public void setSetuped(boolean setuped) {
      this.setuped = setuped;
   }
}
