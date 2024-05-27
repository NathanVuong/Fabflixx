- # General
    - #### Team#: 60
    
    - #### Names: Darren Huynh, Nathan Vuong
    
    - #### Project 4 Video Demo Link: https://youtu.be/_YPRurNd5Yw?si=DBhgCtr7M24xR6LS

    - #### Instruction of deployment: The links of the deployed sites are on the spreadsheet.

    - #### Collaborations and Work Distribution:
    - ncvuong: Worked on fulltext search, adding autocomplete to search bars, and created caching for autocomplete suggestions.
    - dfhuynh: Wored on setting up the connection pooling, masters and slave replication, and load balancing. Worked on general debugging and filmed the demo.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
    - These are all in the folder src
    - AddMovieServlet, AddStarServlet, BrowseResultsServlet, EmployeeLoginServlet, LoginServlet, MovieList, MovieServlet, PaymentServlet, SearchResults, StarServlet
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
    - Connection pooling is utilized in Fablix by reusing the same connection for the moviedb database. This connection pooling is used in all of our servlets that connect to a SQL database and helps us reduce the time it would take to create a new connection every time.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    - Connection pooling works by making it so that all connections toward to the same SQL instance will be reused instead of recreated. Because there are two backend SQL datasources, regardless of where a new connection request may come from, the connection will be reused.
    

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    - AddMovieServlet and AddStarServlet are both always routed to the Master SQL as they need to change the database and have that change be displayed for all instances. All the other Servlets are routed to one or the other (these are the same servlets that are expressed in the connection pooling).

    - #### How read/write requests were routed to Master/Slave SQL?
    - All write requests as in the AddMovieServlet and AddStarServlet are routed to the Master SQL in all instances. All the read requests like MovieListServlet and more are routed to one or the other.
    
