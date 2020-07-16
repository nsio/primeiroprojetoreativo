CREATE TABLE public.usuario
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 10000 CACHE 1 ),
    nome text COLLATE pg_catalog."default",
    email text COLLATE pg_catalog."default",
    login text COLLATE pg_catalog."default",
    password text COLLATE pg_catalog."default",
    CONSTRAINT user_pkey PRIMARY KEY (id),
    CONSTRAINT unique_login UNIQUE (login)
)

TABLESPACE pg_default;

ALTER TABLE public.usuario
    OWNER to postgres;
    
CREATE TABLE public.produto
(
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 10000 CACHE 1 ),
    descricao text COLLATE pg_catalog."default",
    qtd_estoque integer,
    preco numeric,
    id_user bigint,
    CONSTRAINT pk_id PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY (id_user)
        REFERENCES public.usuario (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE public.produto
    OWNER to postgres;