Dim objOnline
Dim objConnector
Set objConnector = CreateObject("OnlineInterface.Connector")
Set objOnline = objConnector.GetOnlineObj
objOnline.Startonline
MsgBox "End"
