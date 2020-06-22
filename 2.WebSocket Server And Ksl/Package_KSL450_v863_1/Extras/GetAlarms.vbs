Dim ret
Dim Filename
Dim objKSLOnline
Dim objKSLData
Dim Lret
Dim SWDENS
Dim SWDENS_STAT
Dim AlarmList
Set objKSLData = CreateObject("KSL450.clsKSLData")
Set objKSLOnline = CreateObject("KSL450.clsOnline")
ret = objKSLData.GetKSLFilename(Filename)
MsgBox Filename
Lret = objKSLOnline.GetSWDData(SWDENS, SWDENS_STAT)
Msgbox "Sea Water Density: " & SWDENS & " ,Sea Water Density Status: " & SWDENS_STAT
ret = objKSLData.GetAlarmList(AlarmList)
MsgBox AlarmList