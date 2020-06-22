Dim objKSLData
Dim ret
Dim Filename
Dim TRef(0)
Dim TTrim(0)
Dim TStatus(0)  
Set objKSLData = CreateObject("KSL450.clsKSLData")
ret = objKSLData.GetKSLFilename(Filename)
MsgBox ret
MsgBox Filename
ret = objKSLData.GetTrimDData(TRef, TTrim, TStatus)
MsgBox ret
MsgBox TRef(0)
MsgBox TTRim(0)
MsgBox TStatus(0)
