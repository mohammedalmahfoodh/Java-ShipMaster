Dim objKSLData
Dim Filename
Dim ret
Set objKSLData = CreateObject("KSL450.clsKSLData")
ret = objKSLData.GetKslFilename(Filename)
MsgBox ret
MsgBox Filename
