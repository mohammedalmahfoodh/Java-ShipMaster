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
	
    Dim TCode()
    Dim Ullage()
    Dim UllCorr()
    Dim ULStat()
    Dim Innage()
    Dim InnCorr()
    Dim INStat()
    Dim Volume()
    Dim VOStat()
    Dim Density()
    Dim DEStat()
    Dim TempH()
    Dim THStat()
    Dim TempM()
    Dim TMStat()
    Dim TempL()
    Dim TLStat()
    Dim Mtemp()
    Dim MTStat()
    Dim Pressure()
    Dim PRStat()
    Dim FlowRate()
    Dim FRStat()

    Redim TCode(NofTanks - 1)
    Redim Ullage(NofTanks - 1)
    Redim UllCorr(NofTanks - 1)
    Redim ULStat(NofTanks - 1)
    Redim Innage(NofTanks - 1)
    Redim InnCorr(NofTanks - 1)
    Redim INStat(NofTanks - 1)
    Redim Volume(NofTanks - 1)
    Redim VOStat(NofTanks - 1)
    Redim Density(NofTanks - 1)
    Redim DEStat(NofTanks - 1)
    Redim TempH(NofTanks - 1)
    Redim THStat(NofTanks - 1)
    Redim TempM(NofTanks - 1)
    Redim TMStat(NofTanks - 1)
    Redim TempL(NofTanks - 1)
    Redim TLStat(NofTanks - 1)
    Redim Mtemp(NofTanks - 1)
    Redim MTStat(NofTanks - 1)
    Redim Pressure(NofTanks - 1)
    Redim PRStat(NofTanks - 1)
    Redim FlowRate(NofTanks - 1)
    Redim FRStat(NofTanks - 1)
Do     
    Lret = objKSLOnline.GetTankData3(TCode, Ullage, UllCorr, ULStat, Innage, _
    		InnCorr, INStat, Volume, VOStat, Density, DEStat, _
    		TempH, THStat, TempM, TMStat, TempL, TLStat, Mtemp, MTStat, _
    		Pressure, PRStat, FlowRate, FRStat)
	For ll=1 to 1000

Next
Loop   
	s = ""
	Msgbox "Number of tanks: " & NofTanks
	For i = 1 To NofTanks
		s = s & "Tank number: " & i & " " 
		s = s & "TankCode: " & TCode(i-1) & " "
		s = s & "Ullage: " & Ullage(i-1) & " "
		s = s & "UllCorr: " & UllCorr(i-1) & " "
		s = s & "ULStat: " & INStat(i-1) & " "
		s = s & "Innage: " & Innage(i-1) & " "
		s = s & "InnCorr: " & InnCorr(i-1) & " "
		s = s & "INStat: " & INStat(i-1) & " "
		s = s & "Volume: " & Volume(i-1) & " "
		s = s & "VOStat: " & VOStat(i-1) & " "
		s = s & vbcrlf
		s = s & "Tank number: " & i & " "
		s = s & "Density: " & Density(i-1) & " "
		s = s & "DEStat: " & DEStat(i-1) & " "
		s = s & "TempH: " & TempH(i-1) & " "
		s = s & "THStat: " & THStat(i-1) & " "
		s = s & "TempM: " & TempM(i-1) & " "
		s = s & "TMStat: " & TMStat(i-1) & " "
		s = s & "TempL: " & TempL(i-1) & " "
		s = s & "TLStat: " & TLStat(i-1) & " "
		s = s & "Mtemp: " & Mtemp(i-1) & " "
		s = s & "MTStat: " & MTStat(i-1) & " "
		s = s & vbcrlf
		s = s & "Tank number: " & i & " "
		s = s & "Pressure: " & Pressure(i-1) & " "
		s = s & "PRStat: " & PRStat(i-1) & " "
		s = s & "FlowRate: " & FlowRate(i-1) & " "
		s = s & "FRStat: " & FRStat(i-1) & " "
		s = s & vbcrlf
		If i mod 10 = 0 Then 
			wscript.echo s
			s=""
		End if
	Next
	If len(s) > 0 Then wscript.echo s

