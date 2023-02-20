insert into APPS(APP_NAME)
values ('ewm-main-service');

insert into hits (app_id, uri, ip, timestamp)
values (1, '/events/1', '192.163.0.1', '2023-02-18 12:00:00'),
       (1, '/events/1', '192.163.0.2', '2023-01-18 12:00:00'),
       (1, '/events/2', '192.163.0.2', '2023-02-10 12:00:00');