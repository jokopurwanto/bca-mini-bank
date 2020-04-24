ALTER TABLE TB_REKENING ADD TRANSAKSI_HARIAN INT NOT NULL;

INSERT INTO TB_JNS_TAB VALUES
(
    1,
    'Cepat Kaya',
    10000,
    5000000,
    0.1
);    

INSERT INTO TB_JNS_TAB VALUES
(
    2,
    'Ekspreso',
    10000,
    10000000,
    0.001
);    


INSERT INTO TB_JNS_TAB VALUES
(
    3,
    'Paket hemat',
    0,
    1000000,
    0.0001
);    

COMMIT WORK;