`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2023/01/19 15:59:30
// Design Name: 
// Module Name: DM
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////


module DM(
    input[31:0] RAddr,
    input[7:0] ASID,
    input[31:0] WData,
    input[31:0] WAddr,
    
    output[31:0] Out
    );
    
    reg [31:0]mem[4095:0];
    
    assign Out = mem[RAddr];
    
endmodule
