create TABLE DbPatches
(
    patch_number INT    NOT NULL,
    applied_date BIGINT NOT NULL
);

create TABLE Test
(
    num  INT     NOT NULL,
    text VARCHAR NOT NULL,
);
