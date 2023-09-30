import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './Customer.jsx'
import {ChakraProvider, createStandaloneToast} from "@chakra-ui/react"
import './index.css'
import {DevSupport} from "@react-buddy/ide-toolbox";
import {ComponentPreviews, useInitial} from "./dev/index.js";
import {createBrowserRouter, RouterProvider} from "react-router-dom";
import Login from "./components/login/Login.jsx";
import AuthProvider from "./components/context/AuthContext.jsx";
import ProtectedRoute from "./components/shared/ProtectedRoute.jsx";
import SignUp from "./components/signup/SignUp.jsx";
import Customer from "./Customer.jsx";
import Home from "./Home.jsx";


const {ToastContainer} = createStandaloneToast()
const router = createBrowserRouter([
    {
        path:"/",
        element: <Login/>
    },
    {
        path: "/signup",
        element: <SignUp/>
    },
    {
        path: "dashboard",
        element: <ProtectedRoute><Home/></ProtectedRoute>
    },
    {
        path: "dashboard/customers",
        element: <ProtectedRoute><Customer/></ProtectedRoute>
    },
])
ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
        <React.StrictMode>
            <ChakraProvider>
                <AuthProvider>
                    <DevSupport ComponentPreviews={ComponentPreviews}
                                useInitialHook={useInitial}>
                        <RouterProvider router={router}/>
                    </DevSupport>
                </AuthProvider>
                <ToastContainer/>
            </ChakraProvider>
        </React.StrictMode>,
    )
