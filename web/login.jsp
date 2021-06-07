<%-- 
    Document   : login
    Created on : 31-May-2021, 13:07:24
    Author     : gagso
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html>
    <head>
        <title>Photo Repository App</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
        <!-- jQuery library -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <!-- Popper JS -->
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
        <!-- Latest compiled JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    </head>
    <body>
        <nav class="navbar navbar-expand-lg navbar-light bg-light justify-content-center">
            <h2>Photo Repository App</h2>
        </nav>
        <div class="container-fluid">
            <div class="h8 text-center">
                Final Assignment - Gagson Lee (20160)
            </div>
            <form action="validate" method="post">
                <div class="row">
                    <label class="col-md-2"></label>
                    <label class="col-md-2">Username:</label>
                    <div class="col-md-6">
                        <input type="text" name="name" class="form-control" required>
                    </div>
                </div>
                <div class="row p-1"></div>
                <div class="row">
                    <label class="col-md-2"></label>
                    <label class="col-md-2">Password:</label>
                    <div class="col-md-6">
                        <input type="password" name="secret" class="form-control" required>
                    </div>
                </div>
                <div class="row p-1"></div>
                <div class="row p-1"></div>
                <div class="row">
                    <label class="col-md-3"></label>
                    <input type="submit" value="Login" class="col-md-6 btn btn-primary btn-block">
                    <label class="col-md-3"></label>

                </div>
            </form>
            <div class="row p-1"></div>
                <div class="row">
                    <label class="col-md-3"></label>
                    <button value="search" name="search" class="col-md-6 btn btn-warning btn-block"><a href=search>Photo Search Page</a></div>
                    <label class="col-md-3"></label>

                </div>
    </body>
</html>
