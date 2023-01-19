`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2023/01/19 12:46:00
// Design Name: 
// Module Name: ALU
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
`define ADD     6'b000010
`define ADDI    6'b000010

module ALU(
    input[31:0] A,
    input[31:0] B,
    input[5:0] Func,
    
    //output overflow,
    output[31:0] Out
    );
    
//    wire[32:0] temp;
    
// 后续可能有关于溢出的处理
//    assign temp = (Func == `ADD || Func == `ADDI) ? {A[31], A} + {B[31], B} : 33'b0;
//    assign overflow = temp[32] ^ temp[31];

    assign Out = (Func == `ADD || Func == `ADDI) ? (A + B) : 32'h0000_0000;
endmodule
