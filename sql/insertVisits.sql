set @patientName = 'h86kim';
set @doctorName = 'kmyin';
set @procedurename = 'yrsfzfdsf';
set @starttime = '2012-09-11 10:12:02.690';
set @stat = 'yep';

INSERT IGNORE ece356_22_2014.Costs(ProcedureName, Cost)
VALUES(@procedurename, FLOOR(RAND() * 9999));

INSERT INTO ece356_22_2014.Visits(
	PatientUsername,
    StartTime,
    ProcedureName,
    DoctorUsername,
    EndTime,
    CurrentStatus,
    PrescriptionStart,
    PrescriptionEnd,
    Diagnosis,
    Prescription,
    Comments,
    ProcedureTime
)
VALUES(
	@patientName,
    @starttime,
    @procedureName,
    @doctorName,
    adddate(@starttime, INTERVAL 1 HOUR),
    @stat,
    adddate(@starttime, INTERVAL 1 DAY),
    adddate(@starttime, INTERVAL 3 DAY),
    @stat,
    @stat,
    @stat,
    adddate(@starttime, INTERVAL 10 DAY)
);

SELECT * FROM ece356_22_2014.Visits
WHERE ProcedureName = @procedureName;
