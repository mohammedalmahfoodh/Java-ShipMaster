Dim ret
Dim Filename
Dim objKSLData
Dim Lret
Set objKSLData = CreateObject("KSL450.clsKSLData")
ret = objKSLData.GetKSLFilename(Filename)
MsgBox Filename

Dim Status
ret = objKSLData.RequestAccept("jmvb1", "00LE1", Status)
MsgBox ret
MsgBox Status
