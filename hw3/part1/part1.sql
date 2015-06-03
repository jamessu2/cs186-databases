DROP VIEW IF EXISTS q1a, q1b, q1c, q1d, q2, q3, q4, q5, q6, q7;

-- Question 1a
CREATE VIEW q1a(id, amount)
AS
  SELECT CC.cmte_id, CC.transaction_amt
  FROM committee_contributions AS CC
  WHERE CC.transaction_amt > 5000
;

-- Question 1b
CREATE VIEW q1b(id, name, amount)
AS
  SELECT CC.cmte_id, CC.name, CC.transaction_amt
  FROM committee_contributions AS CC
  WHERE CC.transaction_amt > 5000
;

-- Question 1c
CREATE VIEW q1c(id, name, avg_amount)
AS
  SELECT CC.cmte_id, CC.name, AVG(CC.transaction_amt)
  FROM committee_contributions AS CC
  WHERE CC.transaction_amt > 5000
  GROUP BY CC.cmte_id, CC.name
;

-- Question 1d
CREATE VIEW q1d(id, name, avg_amount)
AS
  SELECT Averages.cc_id, Averages.cc_name, Averages.cc_avg_trans_amt
  FROM (SELECT CC.cmte_id, CC.name, AVG(CC.transaction_amt)
        FROM committee_contributions AS CC
        WHERE CC.transaction_amt > 5000
        GROUP BY CC.cmte_id, CC.name) AS Averages(cc_id, cc_name, cc_avg_trans_amt)
  WHERE Averages.cc_avg_trans_amt > 10000
;

-- Question 2
CREATE VIEW q2(from_name, to_name)
AS
  SELECT donor_name, acceptor_name
  FROM (SELECT CDonor.name, CAcceptor.name, COUNT(*)
        FROM committees AS CDonor, committees AS CAcceptor, intercommittee_transactions AS IT
        WHERE CDonor.id = IT.other_id
          AND CDonor.pty_affiliation = 'DEM'
          AND CAcceptor.id = IT.cmte_id
          AND CAcceptor.pty_affiliation = 'DEM'
        GROUP BY CDonor.name, CAcceptor.name) AS Counted(donor_name, acceptor_name, transaction_count)
  ORDER BY transaction_count DESC
  LIMIT 10
;

-- Question 3
CREATE VIEW q3(name)
AS
  SELECT C.name
  FROM committees AS C
  WHERE C.name NOT IN
     (SELECT Comms.name
      FROM candidates AS Cands, committees AS Comms, committee_contributions AS CCs
      WHERE Cands.id = CCs.cand_id
        AND Cands.name = 'OBAMA, BARACK'
        AND Comms.id = CCs.cmte_id)
;

-- Question 4. 
CREATE VIEW q4 (name)
AS
  WITH CorrectCandIDs(cID) AS
   (SELECT CountOfCmtes.candID
    FROM (SELECT CCs.cand_id, COUNT(DISTINCT CCs.cmte_id)
          FROM committee_contributions AS CCs
          GROUP BY CCs.cand_id) AS CountOfCmtes(candID, cmteCount)
    WHERE CountOfCmtes.cmteCount >
         (SELECT COUNT(*) * 0.01
          FROM committees)
   )

  SELECT candidates.name
  FROM CorrectCandIDs INNER JOIN candidates
  ON CorrectCandIDs.cID = candidates.id
;

-- Question 5
CREATE VIEW q5 (name, total_pac_donations) AS
  WITH total_contributions(cmteID, totalDonations) AS 
   (SELECT ICs.cmte_id, SUM(ICs.transaction_amt)
    FROM individual_contributions AS ICs
    WHERE ICs.entity_tp = 'ORG'
    GROUP BY ICs.cmte_id)

  SELECT committees.name, TCs.totalDonations
  FROM committees LEFT OUTER JOIN total_contributions as TCs
  ON committees.id = TCs.cmteID
;

-- Question 6
CREATE VIEW q6 (id) AS
  SELECT CCs.cand_id
  FROM committee_contributions AS CCs
  WHERE CCs.entity_tp = 'PAC'
    AND CCs.cand_id IS NOT NULL

  INTERSECT

  SELECT CCs.cand_id
  FROM committee_contributions AS CCs
  WHERE CCs.entity_tp = 'CCM'
    AND CCs.cand_id IS NOT NULL
;

-- Question 7
CREATE VIEW q7 (cand_name1, cand_name2) AS
  WITH RICommittees(cmte_id, cand_id) AS
   (SELECT CCs.cmte_id, CCs.cand_id
    FROM committee_contributions AS CCs
    WHERE CCs.state = 'RI')
  -- Generates the table of donating committees and accepting candidates,
  -- where the donating committee came from Rhode Island

  SELECT DISTINCT Cand1.name, Cand2.name
  FROM candidates AS Cand1, candidates AS Cand2,
       RICommittees AS RIComms1, RICommittees AS RIComms2
  WHERE RIComms1.cmte_id = RIComms2.cmte_id
    AND RIComms1.cand_id = Cand1.id
    AND RIComms2.cand_id = Cand2.id
    AND Cand1.name != Cand2.name
;
