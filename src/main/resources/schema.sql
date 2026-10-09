CREATE TABLE IF NOT EXISTS turno (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente     VARCHAR(100) NOT NULL,
    servicio    VARCHAR(100) NOT NULL,
    fecha_hora  TIMESTAMP    NOT NULL,
    estado      VARCHAR(20)  NOT NULL
);
