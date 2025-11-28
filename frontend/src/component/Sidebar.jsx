import React from "react";
import { Link } from "react-router-dom";
import ApiService from "../service/ApiService";

const logout = () => {
  ApiService.logout();
};

const Sidebar = () => {
  const isAuth = ApiService.isAuthenticated();
  const isAdmin = ApiService.isAdmin();

  return (
    <div className="sidebar sidebar-gold">
      <h1 className="sidebar-logo">Nexa Fashions</h1>

      <ul className="nav-links">
        {isAuth && (
          <li>
            <Link to="/dashboard">Dashboard</Link>
          </li>
        )}

        {isAuth && (
          <li>
            <Link to="/transaction">Transactions</Link>
          </li>
        )}

        {isAdmin && (
          <li>
            <Link to="/category">Categories</Link>
          </li>
        )}

        {isAdmin && (
          <li>
            <Link to="/product">Products</Link>
          </li>
        )}

        {isAdmin && (
          <li>
            <Link to="/supplier">Suppliers</Link>
          </li>
        )}

        {isAuth && (
          <li>
            <Link to="/purchase">Purchase</Link>
          </li>
        )}

        {isAuth && (
          <li>
            <Link to="/sell">Sell</Link>
          </li>
        )}

        {isAuth && (
          <li>
            <Link to="/profile">Profile</Link>
          </li>
        )}

        {isAuth && (
          <li>
            <Link className="logout-link" onClick={logout} to="/login">
              Logout
            </Link>
          </li>
        )}
      </ul>
    </div>
  );
};

export default Sidebar;
