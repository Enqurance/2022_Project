`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2023/01/17 14:55:00
// Design Name: 
// Module Name: PC
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


module PC(
    input clr,
    input clk,
    input [31:0] in,
    output [31:0] out,
    input CtrlPC
    );
    
    reg [31:0] PCReg = 32'd0;
    
    always @(posedge clr or posedge clk) begin
		if(clr)
 begin
			PCReg <= 32'b0;
		end
		else if(CtrlPC)
 begin
			PCReg <= in;
		end
		else begin
		  PCReg <= PCReg + 4;
		end

	end
    
    assign out = PCReg;
    
endmodule
