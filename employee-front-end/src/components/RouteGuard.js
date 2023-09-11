import React from 'react'
import { Route } from 'react-router-dom'


export default function RouteGuard({ children }) {

    function hasJWT() {
        let flag = false;
        console.log("invoke RouteGuard");
        console.log("invoke RouteGuard"+localStorage.getItem("user"));
  
        //check user has JWT token
        localStorage.getItem("user") ? flag=true : flag=false

       
        return flag
    }


  if (!hasJWT) {
    return <Route to='/' />
  }

  return children;
}