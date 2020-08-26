create table owner (
    id UUID PRIMARY KEY not null,
    name varchar(250) not null,
    last_name varchar(250) not null,
    cpf varchar(10) not null,
    birth_date   timestamp(6) without time zone
);


create table account (
    id UUID PRIMARY KEY not null,
    balance numeric not null,
    last_withdraw_date timestamp(6) without time zone,
    withdraw_count numeric not null,
    owner_id UUID not null,
    CONSTRAINT account_owner_fk FOREIGN KEY (owner_id) REFERENCES owner(id)
);

create table save_account (
    id UUID PRIMARY KEY not null,
    balance numeric not null,
    last_transfer_date timestamp(6) without time zone,
    transfer_count numeric not null,
    last_transaction timestamp(6) without time zone,
    user_id UUID not null,
    CONSTRAINT save_account_owner_fk FOREIGN KEY (owner_id) REFERENCES owner(id)
);

