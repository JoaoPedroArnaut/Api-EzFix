INSERT INTO USUARIOS(email, senha) VALUES('teste@email.com', '$2a$10$yq5Qce.6SReNowlO.x6IAOk5yYV8EHdrTkvdnbucuHxFnBHiF6wSK');

INSERT INTO SOLICITANTES(cpf, nome, data_nascimento, telefone_primario, telefone_secundario, usuarios_email) VALUES('49502294866', 'joao', '2001-08-01', 11988724342, 1127122404,'teste@email.com');

INSERT INTO ENDERECOS(id,cep,numero)VALUES (1,03185050,761);

INSERT INTO SOLICITANTES_ENDERECOS(solicitantes_cpf,enderecos_id,enderecos_cep)VALUES ('49502294866',1,03185050)