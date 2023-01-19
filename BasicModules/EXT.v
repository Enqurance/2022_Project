module EXT (
    input [15:0] imm_16,
    output reg [31:0] imm_32,
    input [1:0] EXTOp
);
    always @(*) begin
        case (EXTOp)
            2'b00:
            begin
                imm_32={16'b0,imm_16};
            end
            2'b01:
            begin
                imm_32={{16{imm_16[15]}},imm_16};
            end
            2'b10:
            begin   
                imm_32={imm_16,16'b0};
            end
        endcase
    end
endmodule