# 091209 PR Control file for MBMASTER error handling
"Offset","0"
"Modulo","16"
# "FailPos4" means Modicon address 40003, that is 4th MOBUS word. If this is 60000 this means Auxitrol marks this as ERROR. Pressure
"FailPos4","60000"
# "FailPos5" means Modicon address 40004, that is 5th MOBUS word. If this is 60000 this means Auxitrol marks this as ERROR. Ullage
"FailPos5","60000"
# "FailPos6" means Modicon address 40005, that is 6th MOBUS word. If this is 65036 (-500) this means Auxitrol marks this as ERROR. Temperature
"FailPos6","65036"
