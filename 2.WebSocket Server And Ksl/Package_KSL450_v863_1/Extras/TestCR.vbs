Dim objCRM
Dim objCRes
Set objCRM = CreateObject("CalcResults.ResMain")
Set objCRes = objCRM.ResObj
MsgBox "Start"
objCRes.PopulateCalcresults
Msgbox objCRes.DraftFP
Msgbox objCRes.DraftAP
MsgBox "End"
