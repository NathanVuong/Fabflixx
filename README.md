ncvuong: Created employee login, employee table, and stored procedure to add movies. Created all things related to the employee dashboard and all features for employees. Added recaptcha, worked on HTTPS. General debugging.

dfhuynh: Worked on encryption for customers and employees. Woked on XML parsing and data insertion, the optmizations, and the inconsitency decisions. Filmed the demo and launched the AWS instance. General debugging.

Servlets w/ Prepared Statements: AddMovie, AddStar, BatchInsert, BrowseResults, EmployeeLogin, Login, MovieList, Movie, Payment, SearchResults, Star

Two parsing time strategies we used: We used batch insert when populating the database for everything we inserted. We used hashmaps to map in memory stars, movies, and genres to their respective ids so that it would be quicker to check if they existed and also return the necessary id needed to insert (like with stars_in_movies).

43 movies inconsistent.
0 movies missing ids.
13 movies missing titles.
18 movies missing years.
Inserted 12005 movies.
67 movies duplicated.
Inserted 122 genres.
Inserted 9826 genres_in_movies.
Inserted 6863 stars.
0 stars duplicated.
Inserted 29790 stars_in_movies.
18576 stars missing when doing cast.
998 movies missing when doing cast.
Total time: 32.184s

Inconsistency decisions: If a movie was missing an id, title, or year we would not add it. We also checked to make sure the year of the movie was over 1900 and under 2025. If a movie was had a duplicated id then we would not add it. We basically added all stars and allowed duplicate names. We also added regardless of if they had a dob. We inserted all genres that had names different from the ones in the database. We inserted all stars_in_movies where the stars and movies existed.

P1 Demo: https://youtu.be/0wGPsqCshrc
P2 Demo: https://youtu.be/Gq0kMFww_28?si=7J1Llh_UI6GzBlsv
P3 Demo: https://youtu.be/GL8H_La8fAY?si=SDCJC1K53-zEvEMu
