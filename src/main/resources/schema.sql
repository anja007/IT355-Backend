CREATE TABLE category (
                          category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          category_name VARCHAR(255)
);

CREATE TABLE places (
                        place_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        category_id BIGINT NOT NULL,
                        name VARCHAR(255),
                        address VARCHAR(255),
                        city VARCHAR(255),
                        rating DOUBLE,
                        lat DOUBLE,
                        lng DOUBLE,
                        CONSTRAINT fk_place_category FOREIGN KEY (category_id) REFERENCES category(category_id)
);

CREATE TABLE place_tags (
                            place_place_id BIGINT NOT NULL,
                            tags VARCHAR(255)
);

CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       first_name VARCHAR(255),
                       last_name VARCHAR(255),
                       email VARCHAR(255),
                       username VARCHAR(255),
                       password VARCHAR(255),
                       role VARCHAR(50)
);

CREATE TABLE user_favorites (
                                user_id BIGINT NOT NULL,
                                place_id BIGINT NOT NULL,
                                PRIMARY KEY (user_id, place_id),
                                CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                                CONSTRAINT fk_fav_place FOREIGN KEY (place_id) REFERENCES places(place_id)
);

CREATE TABLE reviews (
                         review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         place_id BIGINT NOT NULL,
                         rating INT,
                         comment TEXT,
                         created_at TIMESTAMP,
                         CONSTRAINT fk_review_user FOREIGN KEY (user_id) REFERENCES users(user_id),
                         CONSTRAINT fk_review_place FOREIGN KEY (place_id) REFERENCES places(place_id)
);
