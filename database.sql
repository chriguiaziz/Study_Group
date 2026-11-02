CREATE DATABASE IF NOT EXISTS studygroup_app_db;
USE studygroup_app_db;

-- Hibernate creates and updates these tables automatically when the app runs.
-- This file documents the relational design expected by the StudyGroup project.

CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    university VARCHAR(255),
    major VARCHAR(255),
    bio VARCHAR(800),
    avatar_url VARCHAR(255),
    role VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    description VARCHAR(300),
    academic_level VARCHAR(80),
    track VARCHAR(120),
    accent_color VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS study_groups (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(1200) NOT NULL,
    owner_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    format VARCHAR(255) NOT NULL,
    rhythm VARCHAR(255),
    visibility VARCHAR(255) NOT NULL,
    subject VARCHAR(120),
    capacity INT NOT NULL,
    free BIT NOT NULL,
    price DECIMAL(38, 2),
    meeting_link VARCHAR(255),
    location VARCHAR(255),
    CONSTRAINT fk_groups_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_groups_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE IF NOT EXISTS memberships (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    role VARCHAR(255),
    status VARCHAR(255),
    CONSTRAINT fk_memberships_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_memberships_group FOREIGN KEY (group_id) REFERENCES study_groups(id)
);

CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    content VARCHAR(1600) NOT NULL,
    sender_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id),
    CONSTRAINT fk_messages_group FOREIGN KEY (group_id) REFERENCES study_groups(id)
);

CREATE TABLE IF NOT EXISTS resources (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_kind VARCHAR(31) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(140) NOT NULL,
    description VARCHAR(700),
    owner_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    url VARCHAR(600),
    file_name VARCHAR(255),
    storage_path VARCHAR(255),
    CONSTRAINT fk_resources_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_resources_group FOREIGN KEY (group_id) REFERENCES study_groups(id)
);

CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    title VARCHAR(120) NOT NULL,
    body VARCHAR(500) NOT NULL,
    type VARCHAR(255),
    read_status BIT NOT NULL,
    recipient_id BIGINT NOT NULL,
    group_id BIGINT,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES users(id),
    CONSTRAINT fk_notifications_group FOREIGN KEY (group_id) REFERENCES study_groups(id)
);
