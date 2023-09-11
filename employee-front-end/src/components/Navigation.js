
import React from 'react';
import { Component } from 'react';
import Container from 'react-bootstrap/Container';
import Nav from 'react-bootstrap/Nav';
import Navbar from 'react-bootstrap/Navbar';
import 'bootstrap/dist/css/bootstrap.css';
import AuthService from '../services/auth.service';

class Navigation extends Component {
    constructor(props) {
        super(props);
        // this.logOut = this.logOut.bind(this);

        this.state = {
            currentUser: undefined,
        };
    }

    componentDidMount() {
        const user = AuthService.getCurrentUser();

        if (user) {
            this.setState({
                currentUser: user,
            });
        }
        /*
                EventBus.on("logout", () => {
                    this.logOut();
                });
                */
    }
    /*
        componentWillUnmount() {
            EventBus.remove("logout");
        }
    */
    logOut() {
        AuthService.logout();
        this.setState({
            currentUser: undefined,
        });
    }

    render() {
        const { currentUser } = this.state;

        return (
            <Navbar id="header" collapseOnSelect expand="xl" bg="black" variant="dark" sticky="top">

                <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                <Navbar.Collapse id="responsive-navbar-nav" fluid="true">
                    <Nav className="me-auto align-center" fluid="true">
                        <Nav.Link href="/" fluid="true" ><h3>Login Form</h3></Nav.Link>
                        {currentUser && (
                            <Nav.Link href="#/app" fluid="true" alt=""><h3>Pair employees</h3></Nav.Link>
                        )}
                    </Nav>

                </Navbar.Collapse>
                {currentUser && (
                    <><a className="userInfo" style={{ color: "white" }}>
                        {currentUser}
                    </a>
                        <a style={{ color: "white" }} href="/" className="nav-link" onClick={this.logOut}>
                            LogOut
                        </a></>
                )}
            </Navbar>






        );
    }
}

export default Navigation;