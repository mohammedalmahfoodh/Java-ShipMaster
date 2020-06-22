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

	
    Dim Status
    Lret = objKSLData.RequestSetVal("jmvb1", 1, "LE", "11", Status)
    Lret = objKSLData.RequestSetVal("jmvb1", 1, "LF", "81", Status)
    Lret = objKSLData.RequestSetVal("jmvb1", 2, "LE", "12", Status)
    Lret = objKSLData.RequestSetVal("jmvb1", 2, "LF", "82", Status)
    Lret = objKSLData.RequestSetVal("jmvb1", 6, "LE", "13", Status)
    Lret = objKSLData.RequestSetVal("jmvb1", 6, "LF", "83", Status)
   

