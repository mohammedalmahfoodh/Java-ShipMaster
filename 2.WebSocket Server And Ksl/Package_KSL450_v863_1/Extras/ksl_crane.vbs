Dim ret
Dim Filename
Dim s
Dim KSLInit
Dim NofCraneItems
Dim objKSLOnline
Dim objKSLData
Dim Lret
Dim i

Set objKSLData = CreateObject("KSL450.clsKSLData")
Set objKSLOnline = CreateObject("KSL450.clsOnline")
ret = objKSLData.GetKSLFilename(Filename)
MsgBox "Configuration file: " & Filename

	
    NofCraneItems = objKSLOnline.GetNumberOfCraneItems()

MsgBox "NofCraneItems: " & NofCraneItems

    Dim CraneNo()
    Dim HookNo()
    Dim COVCG()
    Dim COVCGStat()
    Dim COTCG()
    Dim COTCGStat()
    Dim COLCG()
    Dim COLCGStat()
    Dim CLWeight()
    Dim CLWeightStat()
    Dim CLVCG()
    Dim CLVCGStat()
    Dim CLTCG()
    Dim CLTCGStat()
    Dim CLLCG()
    Dim CLLCGStat()

    Redim CraneNo(NofCraneItems - 1)
    Redim HookNo(NofCraneItems - 1)
    Redim COVCG(NofCraneItems - 1)
    Redim COVCGStat(NofCraneItems - 1)
    Redim COTCG(NofCraneItems - 1)
    Redim COTCGStat(NofCraneItems - 1)
    Redim COLCG(NofCraneItems - 1)
    Redim COLCGStat(NofCraneItems - 1)
    Redim CLWeight(NofCraneItems - 1)
    Redim CLWeightStat(NofCraneItems - 1)
    Redim CLVCG(NofCraneItems - 1)
    Redim CLVCGStat(NofCraneItems - 1)
    Redim CLTCG(NofCraneItems - 1)
    Redim CLTCGStat(NofCraneItems - 1)
    Redim CLLCG(NofCraneItems - 1)
    Redim CLLCGStat(NofCraneItems - 1)

'Do     
    Lret = objKSLOnline.GetCraneItemsData(CraneNo, HookNo , _
    COVCG , COVCGStat , _
    COTCG , COTCGStat , _
    COLCG , COLCGStat , _
    CLWeight , CLWeightStat , _
    CLVCG , CLVCGStat , _
    CLTCG , CLTCGStat , _
    CLLCG , CLLCGStat)
'	Msgbox "LRET: " & Lret

'	For ll=1 to 1000

'Next
'Loop   
	s = ""
'	Msgbox "Number of NofCraneItems: " & NofCraneItems
	For i = 1 To NofCraneItems
		s = s & "CraneItem Number: " & i & " " 
		s = s & "CraneNo: " & CraneNo(i-1) & " "
		s = s & "HookNo: " & HookNo(i-1) & " "
		s = s & "COVCG: " & COVCG(i-1) & " "
'		s = s & "COVCGStat: " & COVCGStat(i-1) & " "
'		s = s & vbcrlf
		s = s & "COTCG: " & COTCG(i-1) & " "
'		s = s & "COTCGStat: " & COTCGStat(i-1) & " "
		s = s & "COLCG: " & COLCG(i-1) & " "
'		s = s & "COLCGStat: " & COLCGStat(i-1) & " "
		s = s & "CLWeight: " & CLWeight(i-1) & " "
'		s = s & "CLWeightStat: " & CLWeightStat(i-1) & " "
'		s = s & vbcrlf
'		s = s & "CraneItem Number: " & i & " "
		s = s & "CLVCG: " & CLVCG(i-1) & " "
'		s = s & "CLVCGStat: " & CLVCGStat(i-1) & " "
		s = s & "CLTCG: " & CLTCG(i-1) & " "
'		s = s & "CLTCGStat: " & CLTCGStat(i-1) & " "
		s = s & "CLLCG: " & CLLCG(i-1) & " "
'		s = s & "CLLCGStat: " & CLLCGStat(i-1) & " "
		s = s & vbcrlf
		If i mod 10 = 0 Then 
			wscript.echo s
			s=""
		End if
	Next
	If len(s) > 0 Then wscript.echo s

