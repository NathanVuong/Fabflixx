USE moviedb;

ALTER TABLE movies
ADD COLUMN price INT;
UPDATE movies
SET price = FLOOR(RAND() * 100) + 1;

ALTER TABLE sales
ADD COLUMN quantity INT;
UPDATE sales
SET quantity = 1;