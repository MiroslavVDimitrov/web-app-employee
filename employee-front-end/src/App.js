import React, { Component, useState, useEffect } from "react";
import Login from "./components/Login";
import axios from "axios";
import {
  BrowserRouter,
  Routes,
  Route,
  Switch,
  Navigate,
  HashRouter,
  redirect

} from "react-router-dom";



import Employee from "./components/Employee";

const API_URL = "http://localhost:8083/";
/* const [sesionStatus, setStatus] = useState("undefined"); */






/* useEffect(() => {
  //Runs on every render
}); */

/* Ако локал сториджа има стойност юзер - Рутера ще позволи да 
се маршрутизира до "/аpp"
*/
const sesionStatus = undefined;
console.log("------sesionStatus : " + sesionStatus);
const isAuthenticated = !!localStorage.getItem("user");




if (sesionStatus === undefined && isAuthenticated) {
  const response = axios
    .get(API_URL + "user", {
      headers: {
        /*   {withCredentials: "true"} */
      }
    }
    );

  if (JSON.stringify(response.headers("statusCode")) === "200") {
    console.log("----------- seseionStatus === 200");

  //  sesionStatus = "200";
    console.log("-----------have response data from check :" + response.sesionStatus);
  }
  else {

    console.log("-----------have response data 500 :" + JSON.stringify(response));
    console.log("-----------have response data 500 :" + JSON.stringify(response.headers));

  };
}
if (sesionStatus === "500") {
  console.log("----------- seseionStatus === 500");
  const response1 = axios
    .post(API_URL + "refreshtoken", {
      headers: {
        /*   {withCredentials: "true"} */
      }
    }
    );

  if ((response1.headers("statusCode")) === "200") {
  //  sesionStatus = "200";
    console.log("-----------have response data from check :" + response1.sesionStatus);
  }
  else {
  //  sesionStatus = undefined;
    localStorage.removeItem("user");
    localStorage.removeItem("sesionInfo");
    console.log("-----------have response data 500 :" + response1.sesionStatus);
  };
}



console.log("App.js user is autonticate -- :" + isAuthenticated);
const user = localStorage.getItem('user');

/* console.log("App.js user Accsess token-- :" + user.accessToken); */

/* Кратък отговор: Ако сайтът ви е статичен, използвайте каквото и да е, няма значение. 
Но ако имате бекенд, използването на хеш маршрутизиране е препоръчителен подход, а не само за реакция.

Обяснение: Когато се използва хеш, само вашето лицево приложение получава заявката,
 не се правят извиквания към бекенда. Това е важно за производствени среди,
  където имате някакъв защитен и/или някакъв обратен прокси (като NGINX и т.н.),
   API шлюз и т.н. Без хеш, заявката ще трябва първо да се обработи от тях и ако не бъде намерена крайна точка,
    заявете се изпраща към интерфейса. Това създава ненужни обаждания, което води до проблеми с производителността, 
    необработени пътища и т.н. А в съвременните облачни среди това означава повече пари.
Трябва да направите навигация от страна на клиента в реагиращия рутер, като използвате методи на рутера,
 а не външни window.location/ history.pushStateмодификации/обаждания. Промяната на хеша е навигация и трябва да се извърши чрез 
 <Link to="#hash">или със useNavigate

 */
const ProtectedRoute = ({ user, children }) => {
  if (!user) {
    return <Navigate to="/" replace />;
    window.location.reload();

  }

  return children;
};

class App extends Component {


  render() {
    return (

      <HashRouter>
        <Routes>

          <Route
            exact
            path="/"
            element={<Login />}
          />
          {/* If user is not logget - Router will redirect to "/" */}

          {/*   <Route
            exact
            path="/app"

            element={
              <ProtectedRoute user={user}>
                <Employee />
              </ProtectedRoute>
            }
          /> */}

          <Route
            exact
            path="/app"

            element={
              isAuthenticated ? <Employee /> : <Login />

            }


          // Това се използва при BrowserRouter
          /*  element={
             isAuthenticated ? <Employee /> : <Navigate to="/" />

           } */
          />


        </Routes>
      </HashRouter>

    );
  }
}

export default App;