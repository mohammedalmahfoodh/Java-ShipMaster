Dim ret
Dim Filename
Dim s
Dim KSLInit
Dim NofTanks
Dim objKSLOnline
Dim objKSLData
Dim Lret
Dim i

Set objKSLData = CreateObject("KSL450.clsKSLData")
Set objKSLOnline = CreateObject("KSL450.clsOnline")
ret = objKSLData.GetKSLFilename(Filename)
MsgBox "Configuration file: " & Filename

	
    NofTanks = objKSLOnline.GetNumberOfTanks()
	
    Dim MaxLevel()
    Dim LevelHAlarmVolpct()
    Dim LevelLAlarmVolpct()

    Redim MaxLevel(NofTanks - 1)
    Redim LevelHAlarmVolpct(NofTanks - 1)
    Redim LevelLAlarmVolpct(NofTanks - 1)
     
    Lret = objKSLData.GetTankAuxData(MaxLevel, LevelHAlarmVolpct, LevelLAlarmVolpct)
   
	s = ""
	Msgbox "Number of tanks: " & NofTanks
	For i = 1 To NofTanks
		s = s & "Tank number: " & i & " " 
		s = s & "MaxLevel: " & MaxLevel(i-1) & " "
		s = s & "LevelHAlarmVolpct: " & LevelHAlarmVolpct(i-1) & " "
		s = s & "LevelLAlarmVolpct: " & LevelLAlarmVolpct(i-1) & " "

		s = s & vbcrlf
		If i mod 10 = 0 Then 
			wscript.echo s
			s=""
		End if
	Next
	If len(s) > 0 Then wscript.echo s

