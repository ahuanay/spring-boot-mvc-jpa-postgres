CREATE TABLE empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    puesto VARCHAR(100) NOT NULL,
    salario NUMERIC(10, 2) NOT NULL,
    fecha_ingreso DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100),
    deleted_by VARCHAR(100)
);

CREATE UNIQUE INDEX idx_empleados_email_unique_active ON empleados(email) WHERE deleted_at IS NULL;
CREATE INDEX idx_empleados_email ON empleados(email);
CREATE INDEX idx_empleados_deleted_at ON empleados(deleted_at);

COMMENT ON TABLE empleados IS 'Tabla que almacena la información de los empleados de la organización.';

COMMENT ON COLUMN empleados.id IS 'Identificador único autoincremental del empleado.';
COMMENT ON COLUMN empleados.nombre IS 'Nombre o nombres del empleado.';
COMMENT ON COLUMN empleados.apellido IS 'Apellidos del empleado.';
COMMENT ON COLUMN empleados.email IS 'Correo electrónico corporativo o personal (usado como clave única de contacto).';
COMMENT ON COLUMN empleados.puesto IS 'Cargo o rol que desempeña el empleado dentro de la empresa (mapeado desde el enum).';
COMMENT ON COLUMN empleados.salario IS 'Remuneración económica del empleado (hasta 10 dígitos en total, con 2 decimales).';
COMMENT ON COLUMN empleados.fecha_ingreso IS 'Fecha en la que el empleado comenzó a laborar en la institución.';
COMMENT ON COLUMN empleados.created_at IS 'Fecha y hora exacta en la que se registró el empleado en la base de datos (Auditoría de creación).';
COMMENT ON COLUMN empleados.updated_at IS 'Fecha y hora de la última modificación o actualización de los datos del empleado (Auditoría de actualización).';
COMMENT ON COLUMN empleados.deleted_at IS 'Fecha y hora en la que se realizó el borrado lógico (soft delete) del empleado. Si es nulo, el registro está activo.';
COMMENT ON COLUMN empleados.created_by IS 'Usuario o sistema que creó el registro (Auditoría de creación).';
COMMENT ON COLUMN empleados.updated_by IS 'Usuario o sistema que realizó la última actualización del registro (Auditoría de actualización).';
COMMENT ON COLUMN empleados.deleted_by IS 'Usuario o sistema que ejecutó el borrado lógico del empleado.';