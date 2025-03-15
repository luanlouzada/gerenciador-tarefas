CREATE TYPE status_type AS ENUM ('Em andamento', 'Pendente', 'Concluído', 'Rascunho', 'Expirado');

CREATE TABLE IF NOT EXISTS "tasks" (
                                       id UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
    user_id UUID NOT NULL REFERENCES "users"(id) ON DELETE RESTRICT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status status_type NOT NULL DEFAULT 'Rascunho',
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    due_at TIMESTAMP NOT NULL
    );

CREATE INDEX idx_tasks_user_id ON tasks(user_id);
CREATE INDEX idx_tasks_due_at ON tasks(due_at);
CREATE INDEX idx_tasks_title ON tasks(title);
