create table post(
  id serial primary key, -- первичный ключ
  name varchar(255), -- заголовок поста
  link varchar(255) NOT NULL UNIQUE, -- ссылка на пост, под одним url не может быть дубликатов
  description text, -- текст вакансии
  created timestamp -- дата создания поста
);

select * from post;