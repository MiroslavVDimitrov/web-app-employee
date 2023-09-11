import React, { useState, useRef, Component, useEffect } from "react";
import { useNavigate, redirect, Link } from "react-router-dom";
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import "bootstrap/dist/css/bootstrap.min.css";
import AuthService from "../services/auth.service";
import Navigation from "./Navigation";
import { Container, Navbar, Row, Col, } from "react-bootstrap";
import Button from 'react-bootstrap/Button';
import { Helmet } from "react-helmet";

//const LOGIN_URL = "http://localhost:8083/";

const required = (value) => {
  if (!value) {
    return (
      <div className="invalid-feedback d-block">
        This field is required!
      </div>
    );
  }
};



class Login extends Component {
  constructor(props) {
    super(props);
    this.handleLogin = this.handleLogin.bind(this);
    this.onChangeUsername = this.onChangeUsername.bind(this);
    this.onChangePassword = this.onChangePassword.bind(this);

    this.state = {
      username: "",
      password: "",
      loading: false,
      message: ""
    };

  }

  /*  loginUser = () => {
     
      const navigate = useNavigate();
    
      useEffect(() => {
       
          navigate("#/app");
        
      });
    } */

  onChangeUsername(e) {
    this.setState({
      username: e.target.value
    });
  }

  onChangePassword(e) {
    this.setState({
      password: e.target.value
    });
  }

  handleLogin(e) {
    e.preventDefault();

    this.setState({
      message: "",
      loading: true
    });

    this.form.validateAll();

    if (this.checkBtn.context._errors.length === 0) {
      /*     const navigate = Navigate.useNavigate();
         */

      AuthService.login(this.state.username, this.state.password).then(
        () => {


/* Използва се пренасочване към  "#/app", когато се рутира със HashRouter*/
          window.location.hash = "#/app";
          window.location.reload();
          /*   Login.loginUser(); */


          /* Пренасочва страницата към "/app"
            - Ако трябва да рефрешна изпозвам = window.location.reload();
          */
          //  window.location.href = LOGIN_URL+"#/app";


          /*  const navigate = useNavigate();
               navigate("#/app"); */
          // window.location.reload();
        },
        error => {
          const resMessage =
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString();

          this.setState({
            loading: false,
            message: resMessage
          });
        }
      );
    } else {
      this.setState({
        loading: false
      });
    }
  }

  render() {
    /*  if (localStorage.getItem("user")) {
       return <Navigate to="/app" />;
     } */

    return (

      <Form
        onSubmit={this.handleLogin}
        ref={c => {
          this.form = c;
        }}
      >
        <Helmet>
          <style>{'body { background-color: black; }'}</style>
        </Helmet>
        {/*   <Navigation /> */}
        <h1></h1>
        <Container bg="black" className="d-flex justify-content-center">
          <img
            src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"
            alt="profile-img"
            className="profile-img-card"
          />

        </Container>
        <h1></h1>
        <Container className="d-flex justify-content-center">
          <Row>
            <Col></Col>
            <Col>
              <label

                style={{ color: "white" }}
                className="d-flex justify-content-end "
                htmlFor="username">Username  </label>
            </Col>
            <Col xs={7}>
              <Input
                type="text"
                className="form-control float-start "
                name="username"
                value={this.state.username}
                onChange={this.onChangeUsername}
                validations={[required]}
              />
            </Col>
            <Col></Col>
          </Row>


        </Container>
        <h1></h1>
        <Container className="d-flex justify-content-center">
          <Row>
            <Col>
            </Col>
            <Col>
              <label
                style={{ color: "white" }}
                htmlFor="password">Password</label>
            </Col>
            <Col xs={7}>
              <Input
                type="password"
                className="form-control"
                name="password"
                value={this.state.password}
                onChange={this.onChangePassword}
                validations={[required]}
              />
            </Col>
            <Col>
            </Col>
          </Row>

        </Container>
        <h1></h1>
        <Container className="d-flex justify-content-center">
          <button
            className="btn btn-primary align-items-center "

            disabled={this.state.loading}
          >
            {this.state.loading && (
              <span className="spinner-border spinner-border-sm"></span>
            )}
            <span>Login</span>
          </button>
        </Container>
        <h1></h1>
        <Container className="d-flex justify-content-center">
          {this.state.message && (
            <div className="form-group">
              <div className="alert alert-danger" role="alert">
                {this.state.message}
              </div>
            </div>
          )}
        </Container>
        <Container className="d-flex justify-content-center">
          <CheckButton
            className="align-items-center"
            style={{ display: "none" }}
            ref={c => {
              this.checkBtn = c;
            }}
          />
        </Container>





      </Form>

    );
  }
}

export default Login;