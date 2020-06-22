Dim ret
Dim objKSLOnline
Dim sAct()
ReDim sAct(0)
sAct(0)="Quit"
Set objKSLOnline = CreateObject("KSL450.clsOnline")
ret = objKSLOnline.Execute(sAct)
MsgBox ret
Set objKSLOnline = Nothing
